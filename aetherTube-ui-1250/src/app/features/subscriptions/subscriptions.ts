import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoCard, Video } from '../../shared/components/video-card/video-card';

@Component({
  selector: 'app-subscriptions',
  standalone: true,
  imports: [CommonModule, VideoCard],
  templateUrl: './subscriptions.html',
})
export class Subscriptions {
  viewMode: 'grid' | 'list' = 'grid';

  videos: Video[] = Array(15).fill(null).map((_, i) => ({
    id: i.toString(),
    thumbnail: `https://picsum.photos/seed/${i + 900}/640/360`,
    duration: `${Math.floor(Math.random() * 20)}:${Math.floor(Math.random() * 60).toString().padStart(2, '0')}`,
    avatar: `https://i.pravatar.cc/150?u=${i + 100}`,
    title: `Subscription Update #${i + 1}: Catch up on the latest content!`,
    channelName: `Subscribed Channel ${i + 1}`,
    views: `${Math.floor(Math.random() * 200 + 1)}K`,
    uploadTime: `${i + 1} hours ago`
  }));
}
