import { Component, Input, OnInit, numberAttribute } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VideoPlayer } from '../../features/video-player/video-player';

@Component({
    selector: 'app-watch',
    standalone: true,
    imports: [CommonModule, VideoPlayer],
    templateUrl: './watch.html',
})
export class WatchComponent {
    @Input() id!: string; // Route param 'id'

    ngOnInit() {
        console.log('WatchComponent initialized with ID:', this.id);
    }
}
