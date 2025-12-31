import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RouterModule } from '@angular/router';

export interface Video {
  id: string;
  thumbnail: string;
  duration: string;
  avatar: string;
  title: string;
  channelName: string;
  views: string;
  uploadTime: string;
}

@Component({
  selector: 'app-video-card',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './video-card.html',
})
export class VideoCard {
  @Input() video!: Video;

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://placehold.co/640x360?text=No+Thumbnail'; // Fallback
  }
}
