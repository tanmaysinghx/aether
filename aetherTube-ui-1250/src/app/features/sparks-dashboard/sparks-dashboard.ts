import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SparkPlayer } from './components/spark-player/spark-player';

@Component({
  selector: 'app-sparks-dashboard',
  standalone: true,
  imports: [CommonModule, SparkPlayer],
  templateUrl: './sparks-dashboard.html',
})
export class SparksDashboard {
  constructor() {
    console.log('SparksDashboard Initialized');
  }
  sparks = Array(10).fill(null).map((_, i) => ({
    id: i.toString(),
    thumbnail: `https://picsum.photos/seed/${i + 700}/360/640`,
    title: `Amazing Spark #${i + 1} - Watch this!`,
    channelName: `Creator ${i + 1}`,
    avatar: `https://i.pravatar.cc/150?u=${i + 50}`,
    likes: `${Math.floor(Math.random() * 50 + 1)}K`,
    comments: `${Math.floor(Math.random() * 500)}`
  }));
}
