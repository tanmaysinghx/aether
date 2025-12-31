import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { formatDistanceToNow } from 'date-fns';
import { VideoCard, Video } from '../../shared/components/video-card/video-card';
import { SparkCard } from '../../shared/components/spark-card/spark-card';
import { VideoService, VideoFeedItem } from '../../core/services/video.service';
import { VideoCardSkeleton } from '../../shared/components/skeletons/video-card-skeleton/video-card-skeleton';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, VideoCard, SparkCard, VideoCardSkeleton],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private videoService = inject(VideoService);

  categories = ['All', 'Gaming', 'Music', 'Live', 'Sparks', 'Tech', 'News', 'Programming', 'Podcasts', 'Comedy', 'Action', 'Recently uploaded', 'New to you'];
  selectedCategory = 'All';

  isLoading = signal(true);
  videos = signal<Video[]>([]);

  // Mock Sparks for now
  sparks = Array(6).fill(null).map((_, i) => ({
    id: i.toString(),
    thumbnail: `https://picsum.photos/seed/${i + 500}/360/640`,
    title: `Epic Spark Moment #${i + 1}`,
    views: `${Math.floor(Math.random() * 500 + 1)}K`
  }));

  ngOnInit() {
    this.videoService.getFeed().subscribe({
      next: (response) => {
        if (response.success) {
          const mappedVideos = response.data.map(item => this.mapToVideo(item));
          this.videos.set(mappedVideos);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to fetch feed', err);
        this.isLoading.set(false);
      }
    });

    // Mock loading delay to show shimmer
    /*
    setTimeout(() => {
        // above logic
    }, 2000);
    */
  }

  private mapToVideo(item: VideoFeedItem): Video {
    return {
      id: item.id,
      thumbnail: `http://localhost:1205/${item.thumbnailUrl}`, // Adjust base URL as needed
      duration: this.formatDuration(item.durationSeconds),
      avatar: `https://i.pravatar.cc/150?u=${item.uploaderId}`, // Mock avatar
      title: item.title,
      channelName: 'Aether User', // Mock name
      views: '0', // Mock views
      uploadTime: formatDistanceToNow(new Date(item.createdAt), { addSuffix: true })
        .replace('about ', '') // Clean up
    };
  }

  private formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  selectCategory(category: string) {
    this.selectedCategory = category;
  }
}
