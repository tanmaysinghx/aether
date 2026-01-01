import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-studio-stats',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6">
      <h1 class="text-2xl font-bold mb-6 text-text-main dark:text-text-dark-main">Channel Dashboard</h1>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Subscribers Station -->
        <div class="bg-surface dark:bg-surface-dark border border-border dark:border-border-dark rounded-xl p-6 shadow-sm">
          <h3 class="text-sm font-medium text-text-muted mb-2">Total Subscribers</h3>
          <p class="text-3xl font-bold text-text-main dark:text-text-dark-main">1,234</p>
          <span class="text-sm text-green-500 flex items-center mt-2">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
            +12 this week
          </span>
        </div>

        <!-- Views Station -->
        <div class="bg-surface dark:bg-surface-dark border border-border dark:border-border-dark rounded-xl p-6 shadow-sm">
          <h3 class="text-sm font-medium text-text-muted mb-2">Total Views</h3>
          <p class="text-3xl font-bold text-text-main dark:text-text-dark-main">45.2K</p>
          <span class="text-sm text-green-500 flex items-center mt-2">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
            +5.2%
          </span>
        </div>

        <!-- Watch Time Station -->
        <div class="bg-surface dark:bg-surface-dark border border-border dark:border-border-dark rounded-xl p-6 shadow-sm">
          <h3 class="text-sm font-medium text-text-muted mb-2">Watch Time (hours)</h3>
          <p class="text-3xl font-bold text-text-main dark:text-text-dark-main">2.4K</p>
          <span class="text-sm text-red-500 flex items-center mt-2">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"></path></svg>
            -1.4%
          </span>
        </div>

        <!-- Revenue Station -->
        <div class="bg-surface dark:bg-surface-dark border border-border dark:border-border-dark rounded-xl p-6 shadow-sm">
          <h3 class="text-sm font-medium text-text-muted mb-2">Estimated Revenue</h3>
          <p class="text-3xl font-bold text-text-main dark:text-text-dark-main">$892.40</p>
          <span class="text-sm text-green-500 flex items-center mt-2">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
            +12.5%
          </span>
        </div>
      </div>

      <!-- Recent Videos Section -->
      <div class="bg-surface dark:bg-surface-dark border border-border dark:border-border-dark rounded-xl overflow-hidden shadow-sm">
        <div class="p-6 border-b border-border dark:border-border-dark">
            <h2 class="text-lg font-semibold text-text-main dark:text-text-dark-main">Recent Content</h2>
        </div>
        <div class="p-6 text-center text-text-muted">
            <p>Your latest uploaded content will appear here.</p>
        </div>
      </div>
    </div>
  `
})
export class StudioStats {}
