import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
    selector: 'app-studio-sidebar',
    standalone: true,
    imports: [CommonModule, RouterLink, RouterLinkActive],
    template: `
    <div class="fixed top-0 left-0 h-full w-64 bg-surface dark:bg-surface-dark border-r border-border dark:border-border-dark pt-16 z-40 hidden lg:block">
      <div class="px-4 py-6">
        <div class="space-y-1">
          <!-- Dashboard -->
          <a routerLink="/studio" 
             routerLinkActive="bg-primary/10 text-primary"
             [routerLinkActiveOptions]="{exact: true}"
             class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-text-main dark:text-text-dark-main hover:bg-surface-hover dark:hover:bg-surface-dark-hover transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"></path></svg>
            Dashboard
          </a>

          <!-- Content -->
          <a routerLink="/studio/content" 
             routerLinkActive="bg-primary/10 text-primary"
             class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-text-main dark:text-text-dark-main hover:bg-surface-hover dark:hover:bg-surface-dark-hover transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
            Content
          </a>

          <!-- Analytics (Placeholder) -->
          <a routerLink="/studio/analytics" 
             routerLinkActive="bg-primary/10 text-primary"
             class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-text-main dark:text-text-dark-main hover:bg-surface-hover dark:hover:bg-surface-dark-hover transition-colors opacity-70 cursor-not-allowed">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path></svg>
            Analytics
          </a>

           <!-- Comments (Placeholder) -->
          <a routerLink="/studio/comments" 
             routerLinkActive="bg-primary/10 text-primary"
             class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-text-main dark:text-text-dark-main hover:bg-surface-hover dark:hover:bg-surface-dark-hover transition-colors opacity-70 cursor-not-allowed">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"></path></svg>
            Comments
          </a>
          
          <div class="pt-4 mt-4 border-t border-border dark:border-border-dark">
             <!-- Upload Video -->
            <a routerLink="/studio/upload" 
               routerLinkActive="bg-primary/10 text-primary"
               class="flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium text-text-main dark:text-text-dark-main hover:bg-surface-hover dark:hover:bg-surface-dark-hover transition-colors">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"></path></svg>
              Upload Video
            </a>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: []
})
export class StudioSidebar { }
