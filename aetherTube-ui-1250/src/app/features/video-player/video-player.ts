import { Component, Input, OnInit, OnDestroy, OnChanges, SimpleChanges, ElementRef, ViewChild, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoService } from '../../core/services/video.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import Hls from 'hls.js';

@Component({
    selector: 'app-video-player',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './video-player.html',
    styles: [`
      input[type=range] {
        @apply appearance-none bg-transparent cursor-pointer;
      }
      input[type=range]::-webkit-slider-thumb {
        @apply appearance-none w-3 h-3 bg-primary rounded-full border-2 border-white shadow transition-transform;
      }
      input[type=range]:hover::-webkit-slider-thumb {
        @apply scale-125;
      }
      .progress-bar {
        @apply bg-white/30 hover:h-1.5 transition-all h-1 rounded-full cursor-pointer relative;
      }
    `]
})
export class VideoPlayer implements OnInit, OnDestroy, OnChanges {
    @Input() videoId!: string;
    @ViewChild('videoDisplay') videoElementRef!: ElementRef<HTMLVideoElement>;
    @ViewChild('container') containerRef!: ElementRef<HTMLDivElement>;

    private videoService = inject(VideoService);
    private hls: Hls | null = null;

    // State Signals
    isPlaying = signal(false);
    currentTime = signal(0);
    duration = signal(0);
    volume = signal(1);
    isMuted = signal(false);
    isFullscreen = signal(false);
    isHovering = signal(false);
    isLoading = signal(true);

    // Quality Settings
    qualityLevels = signal<{ height: number, level: number }[]>([]);
    currentLevel = signal<number>(-1); // -1 is Auto
    isSettingsOpen = signal(false);

    ngOnInit() { }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['videoId'] && this.videoId && this.videoElementRef) {
            this.loadVideo(this.videoId);
        }
    }

    ngAfterViewInit() {
        if (this.videoId) {
            this.loadVideo(this.videoId);
        }
    }

    loadVideo(id: string) {
        if (!this.videoElementRef) return;

        const video = this.videoElementRef.nativeElement;
        this.isLoading.set(true);

        // Fetch stream and progress in parallel, but wait for both
        forkJoin({
            streamUrl: this.videoService.getVideoStream(id),
            progress: this.videoService.getProgress(id).pipe(catchError(() => of(null))) // Handle error gracefully
        }).subscribe(({ streamUrl, progress }) => {
            let startSeconds = 0;
            if (progress && progress.success && progress.data?.progressSeconds) {
                console.log('Resuming from:', progress.data.progressSeconds);
                startSeconds = progress.data.progressSeconds;
            }

            if (Hls.isSupported()) {
                if (this.hls) this.hls.destroy();

                // Pass startPosition to HLS config
                this.hls = new Hls({
                    debug: false,
                    startPosition: startSeconds
                });

                this.hls.loadSource(streamUrl);
                this.hls.attachMedia(video);

                this.hls.on(Hls.Events.MANIFEST_PARSED, (_, data) => {
                    const levels = data.levels.map((l, index) => ({ height: l.height, level: index }));
                    this.qualityLevels.set(levels);
                    this.isLoading.set(false);

                    // Explicitly set currentTime just in case
                    if (startSeconds > 0) {
                        video.currentTime = startSeconds;
                    }

                    video.play().catch(() => { });
                });

                this.hls.on(Hls.Events.LEVEL_SWITCHED, (_, data) => {
                    if (this.hls?.autoLevelEnabled) { }
                });

            } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
                video.src = streamUrl;
                this.isLoading.set(false);
                if (startSeconds > 0) {
                    video.currentTime = startSeconds;
                }
                video.addEventListener('loadedmetadata', () => {
                    if (startSeconds > 0) video.currentTime = startSeconds;
                    video.play();
                });
            }
        });
    }

    // --- Controls ---

    // Sync progress every 5 seconds or on pause
    private progressInterval: any;

    startProgressSync() {
        this.stopProgressSync();
        this.progressInterval = setInterval(() => {
            if (this.isPlaying() && this.videoId) {
                this.saveProgress();
            }
        }, 5000);
    }

    stopProgressSync() {
        if (this.progressInterval) {
            clearInterval(this.progressInterval);
        }
    }

    saveProgress() {
        const video = this.videoElementRef.nativeElement;
        if (video.currentTime > 0) {
            this.videoService.saveProgress(this.videoId, video.currentTime).subscribe();
        }
    }

    togglePlay() {
        const video = this.videoElementRef.nativeElement;
        if (video.paused) {
            video.play();
            this.isPlaying.set(true);
            this.startProgressSync();
        } else {
            video.pause();
            this.isPlaying.set(false);
            this.saveProgress(); // Save immediately on pause
            this.stopProgressSync();
        }
    }

    onTimeUpdate() {
        const video = this.videoElementRef.nativeElement;
        this.currentTime.set(video.currentTime);
        this.duration.set(video.duration || 0);
        this.isPlaying.set(!video.paused);
    }

    seek(event: Event) {
        const input = event.target as HTMLInputElement;
        const video = this.videoElementRef.nativeElement;
        video.currentTime = Number(input.value);
    }

    toggleMute() {
        const video = this.videoElementRef.nativeElement;
        video.muted = !video.muted;
        this.isMuted.set(video.muted);
        if (video.muted) this.volume.set(0);
        else this.volume.set(video.volume || 1);
    }

    setVolume(event: Event) {
        const input = event.target as HTMLInputElement;
        const vol = Number(input.value);
        const video = this.videoElementRef.nativeElement;
        video.volume = vol;
        this.volume.set(vol);
        this.isMuted.set(vol === 0);
    }

    toggleFullscreen() {
        const container = this.containerRef.nativeElement;
        if (!document.fullscreenElement) {
            container.requestFullscreen().then(() => this.isFullscreen.set(true));
        } else {
            document.exitFullscreen().then(() => this.isFullscreen.set(false));
        }
    }

    // --- Quality ---

    changeQuality(levelIndex: number) {
        if (this.hls) {
            this.hls.currentLevel = levelIndex;
            this.currentLevel.set(levelIndex);
            this.isSettingsOpen.set(false);
        }
    }

    toggleSettings() {
        this.isSettingsOpen.update(v => !v);
    }

    // --- Formatters ---

    formatTime(seconds: number): string {
        if (!seconds) return '0:00';
        const m = Math.floor(seconds / 60);
        const s = Math.floor(seconds % 60);
        return `${m}:${s.toString().padStart(2, '0')}`;
    }

    ngOnDestroy() {
        this.stopProgressSync();
        if (this.hls) this.hls.destroy();
    }
}
