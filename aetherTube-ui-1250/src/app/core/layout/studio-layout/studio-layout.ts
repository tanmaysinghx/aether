import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Header } from '../../../shared/components/header/header';
import { StudioSidebar } from './studio-sidebar';

@Component({
    selector: 'app-studio-layout',
    standalone: true,
    imports: [CommonModule, RouterOutlet, Header, StudioSidebar],
    template: `
    <div class="bg-background dark:bg-background-dark min-h-screen">
       <app-header></app-header>
       <app-studio-sidebar></app-studio-sidebar>
       
       <main class="pt-16 lg:pl-64 transition-all duration-300">
          <div class="p-6">
             <router-outlet></router-outlet>
          </div>
       </main>
    </div>
  `
})
export class StudioLayout { }
