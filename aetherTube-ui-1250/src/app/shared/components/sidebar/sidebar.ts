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

  adultCategories = [
    { name: 'Dashboard', icon: 'dashboard', link: '/adult/dashboard' },
    { name: 'Live Cams', icon: 'video', link: '/adult/live' },
    { name: 'Premium Videos', icon: 'star', link: '/adult/premium' },
    { name: 'Top Rated', icon: 'trending-up', link: '/adult/top-rated' },
    { name: 'Categories', icon: 'list', link: '/adult/categories' },
    { name: 'Models', icon: 'users', link: '/adult/models' },
    { name: 'Aether Studio', icon: 'layout-dashboard', link: '/studio' }
  ];

  toggleAdultTheme(event: Event) {
    event.preventDefault();
    this.ui.toggleAdultTheme();
  }
}