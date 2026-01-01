import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UiService {
  // signal stores sidebar open state
  sidebarOpen = signal(false);

  toggleSidebar() {
    this.sidebarOpen.update(v => !v);
  }

  openSidebar() {
    this.sidebarOpen.set(true);
  }

  closeSidebar() {
    this.sidebarOpen.set(false);
  }

  // Theme State
  isAdultTheme = signal(localStorage.getItem('adult-theme') === 'true');

  constructor() {
    // Apply theme on init if saved
    if (this.isAdultTheme()) {
      document.body.classList.add('theme-adult');
      document.documentElement.classList.add('dark');
    }
  }

  toggleAdultTheme() {
    this.isAdultTheme.update(v => !v);

    if (this.isAdultTheme()) {
      document.body.classList.add('theme-adult');
      // Force Dark Mode
      document.documentElement.classList.add('dark');
      localStorage.setItem('theme', 'dark');
      localStorage.setItem('adult-theme', 'true');
    } else {
      document.body.classList.remove('theme-adult');
      localStorage.setItem('adult-theme', 'false');
      // Optional: Revert to saved preference or default? 
      // For now, let's leave it in dark mode as that's safer/less jarring, 
      // or user can manually toggle back if they want light mode.
    }
  }
}
