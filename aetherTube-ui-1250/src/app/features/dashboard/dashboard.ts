import { Component, OnInit, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { formatDistanceToNow } from 'date-fns';
import { VideoCard, Video } from '../../shared/components/video-card/video-card';
import { SparkCard } from '../../shared/components/spark-card/spark-card';
import { VideoService, VideoFeedItem } from '../../core/services/video.service';
import { UiService } from '../../shared/services/ui.service';
import { VideoCardSkeleton } from '../../shared/components/skeletons/video-card-skeleton/video-card-skeleton';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, VideoCard, SparkCard, VideoCardSkeleton],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private videoService = inject(VideoService);
  ui = inject(UiService); // Public for template usage if needed

  categories = ['All', 'Gaming', 'Music', 'Live', 'Sparks', 'Tech', 'News', 'Programming', 'Podcasts', 'Comedy', 'Action', 'Recently uploaded', 'New to you'];
  selectedCategory = 'All';

  isLoading = signal(true);
  videos = signal<Video[]>([]);

  // Reactive Sparks state
  sparks = signal<any[]>([]);
  sparksLoading = signal(true);

  constructor() {
    effect(() => {
      const isAdult = this.ui.isAdultTheme();
      this.loadFeed(isAdult ? 'ADULT' : 'TUBE');
      this.loadSparks(); // Always load REELS for now, or could change base on theme if needed
    });
  }

  loadSparks() {
    this.sparksLoading.set(true);
    this.videoService.getFeed('REELS').subscribe({
      next: (response) => {
        if (response.success && response.data) {
          const mappedSparks = response.data.map(item => ({
            id: item.id,
            thumbnail: `http://localhost:1205/api/v1/stream/${item.id}/thumbnail.jpg`,
            title: item.title,
            views: this.formatViewCount(item.viewCount)
          }));
          this.sparks.set(mappedSparks);
        } else {
          this.sparks.set([]);
        }
        this.sparksLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch sparks', err);
        this.sparks.set([]);
        this.sparksLoading.set(false);
      }
    });
  }

  ngOnInit() {
    // Initial load handled by effect
  }

  loadFeed(appSource: string) {
    this.isLoading.set(true);
    this.videoService.getFeed(appSource).subscribe({
      next: (response) => {
        if (response.success) {
          const mappedVideos = response.data.map(item => this.mapToVideo(item));
          this.videos.set(mappedVideos);
        } else {
          this.videos.set([]);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch feed', err);
        this.isLoading.set(false);
        this.videos.set([]); // Ensure empty state on error
      }
    });
  }

  private mapToVideo(item: VideoFeedItem): Video {
    return {
      id: item.id,
      thumbnail: `http://localhost:1205/api/v1/stream/${item.id}/thumbnail.jpg`,
      duration: this.formatDuration(item.durationSeconds),
      avatar: `https://i.pravatar.cc/150?u=${item.uploaderId}`,
      title: item.title,
      channelName: item.uploaderId,
      views: this.formatViewCount(item.viewCount),
      uploadTime: formatDistanceToNow(new Date(item.createdAt), { addSuffix: true })
        .replace('about ', '')
    };
  }

  private formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  private formatViewCount(count?: number): string {
    if (!count) return '0';
    if (count >= 1000000) return `${(count / 1000000).toFixed(1)}M`;
    if (count >= 1000) return `${(count / 1000).toFixed(1)}K`;
    return count.toString();
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }
}
