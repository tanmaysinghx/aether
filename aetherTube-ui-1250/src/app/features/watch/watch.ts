import { Component, Input, OnInit, OnChanges, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { VideoPlayer } from '../../features/video-player/video-player';
import { VideoService, VideoDetails, VideoFeedItem } from '../../core/services/video.service';
import { UiService } from '../../shared/services/ui.service';
import { formatDistanceToNow } from 'date-fns';

@Component({
    selector: 'app-watch',
    standalone: true,
    imports: [CommonModule, VideoPlayer, RouterLink],
    templateUrl: './watch.html',
})
export class WatchComponent implements OnInit, OnChanges {
    @Input() id!: string; // Route param 'id'

    private videoService = inject(VideoService);
    public ui = inject(UiService);

    video = signal<VideoDetails | null>(null);
    nextVideos = signal<VideoFeedItem[]>([]);
    isLoading = signal(true);

    constructor() {
        effect(() => {
            const isAdult = this.ui.isAdultTheme();
            this.loadUpNextVideos();
        });
    }

    ngOnInit() {
        if (this.id) {
            this.loadVideoDetails(this.id);
            this.loadUpNextVideos();
        }
    }

    ngOnChanges() {
        if (this.id) {
            this.loadVideoDetails(this.id);
            this.loadUpNextVideos();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    }

    loadVideoDetails(id: string) {
        this.isLoading.set(true);
        this.videoService.getVideo(id).subscribe({
            next: (res) => {
                if (res.success) {
                    this.video.set(res.data);
                }
                this.isLoading.set(false);
            },
            error: (err) => {
                console.error('Failed to load video details', err);
                this.isLoading.set(false);
            }
        });
    }

    loadUpNextVideos() {
        const source = this.ui.isAdultTheme() ? 'ADULT' : 'TUBE';
        this.videoService.getFeed(source).subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    const filtered = res.data.filter(v => v.id !== this.id).map(v => ({
                        ...v,
                        thumbnailUrl: v.thumbnailUrl && v.thumbnailUrl.startsWith('http')
                            ? v.thumbnailUrl
                            : `http://localhost:1205/api/v1/stream/${v.id}/thumbnail.jpg`
                    }));
                    this.nextVideos.set(filtered);
                }
            }
        });
    }

    formatDate(dateStr: string): string {
        try {
            return formatDistanceToNow(new Date(dateStr), { addSuffix: true });
        } catch {
            return '';
        }
    }

    // Mock Comments
    comments = signal([
        { id: 1, user: 'AlexCodes', avatar: 'https://i.pravatar.cc/150?u=1', text: 'This functionality is actually insane! 🔥', time: '2 hours ago', likes: 124 },
        { id: 2, user: 'SarahJ', avatar: 'https://i.pravatar.cc/150?u=2', text: 'Love the dark mode aesthetic, very sleek.', time: '5 hours ago', likes: 89 },
        { id: 3, user: 'MikeDev', avatar: 'https://i.pravatar.cc/150?u=3', text: 'Can you do a tutorial on how you built the streaming part?', time: '1 day ago', likes: 456 },
        { id: 4, user: 'EmilyW', avatar: 'https://i.pravatar.cc/150?u=4', text: 'First!!', time: '2 days ago', likes: 2 },
        { id: 5, user: 'RogueOne', avatar: 'https://i.pravatar.cc/150?u=5', text: 'Underrated content. Keep it up!', time: '1 week ago', likes: 12 }
    ]);
}
