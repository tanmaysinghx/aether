import { Component, Input, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-spark-player',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './spark-player.html',
})
export class SparkPlayer {
    @Input() spark: any;
    isLiked = signal(false);
    isSubscribed = signal(false);

    toggleLike() {
        this.isLiked.update(v => !v);
    }

    toggleSubscribe() {
        this.isSubscribed.update(v => !v);
    }
}
