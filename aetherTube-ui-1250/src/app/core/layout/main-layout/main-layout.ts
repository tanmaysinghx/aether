import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '../header/header';
import { Sidebar as SidebarComponent } from '../sidebar/sidebar';

@Component({
  selector: 'app-main-layout',
  imports: [Header, SidebarComponent, RouterOutlet],
  templateUrl: './main-layout.html',
  styles: ``
})
export class MainLayout {

}
