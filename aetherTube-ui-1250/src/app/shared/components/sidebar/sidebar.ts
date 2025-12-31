import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { UiService } from '../../services/ui.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class Sidebar {
  subscribedChannels = Array(5).fill(null).map((_, i) => ({
    id: i.toString(),
    name: `Channel ${i + 1}`,
    avatar: `https://i.pravatar.cc/150?u=${i + 200}`,
    link: `/channel/${i}`
  }));

  constructor(public ui: UiService) { }

  unsubscribe(channelId: string, event: Event) {
    event.stopPropagation();
    event.preventDefault(); // Prevent navigation
    // Mock unsubscribe
    this.subscribedChannels = this.subscribedChannels.filter(c => c.id !== channelId);
  }
}