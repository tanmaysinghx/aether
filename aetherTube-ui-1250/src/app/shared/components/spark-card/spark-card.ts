import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-spark-card',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './spark-card.html',
})
export class SparkCard {
    @Input() spark: any;
}
