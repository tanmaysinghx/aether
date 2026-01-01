import { Component, signal, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { VideoService, VideoFeedItem } from '../../../core/services/video.service';
import { formatDistanceToNow } from 'date-fns';

@Component({
    selector: 'app-studio-content',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './studio-content.html',
})
export class StudioContent {
    private videoService = inject(VideoService);

    private pollInterval: any;

    // Videos signal initialized with empty array
    videos = signal<any[]>([]);

    ngOnInit() {
        this.fetchVideos();
        this.startPolling();
    }

    fetchVideos() {
        const newVideoId = this.videoService.lastUploadedVideoId();

        this.videoService.getMyVideos().subscribe({
            next: (response) => {
                if (response.success && response.data) {
                    const fetchedVideos = response.data.map(v => {
                        let status = (v as any).status || 'Uploaded';

                        // Force processing status if this is the newly uploaded video
                        if (newVideoId && v.id.toLowerCase() === newVideoId.toLowerCase()) {
                            console.log('DEBUG: Found new video in feed, forcing status to Processing', v.id);
                            status = 'Processing';
                        }

                        return {
                            ...v,
                            status: status,
                            views: v.viewCount || 0,
                            likes: '--',
                            comments: 0,
                            date: v.createdAt,
                            thumbnail: v.thumbnailUrl ?
                                (v.thumbnailUrl.startsWith('http') ? v.thumbnailUrl : `http://localhost:1205/api/v1/stream/${v.id}/thumbnail.jpg`)
                                : 'assets/placeholder.jpg',
                            visibility: 'Public',
                            progress: 0
                        };
                    });

                    // If new video is missing from feed, add it manually
                    if (newVideoId && !fetchedVideos.find(v => v.id.toLowerCase() === newVideoId.toLowerCase())) {
                        console.log('DEBUG: New video not found in feed, adding manually with ID:', newVideoId);
                        fetchedVideos.unshift({
                            // VideoFeedItem properties
                            id: newVideoId,
                            title: 'Processing Upload...',
                            thumbnailUrl: '',
                            durationSeconds: 0,
                            uploaderId: '',
                            videoType: 'UPLOAD',
                            appSource: 'TUBE',
                            createdAt: new Date().toISOString(),
                            viewCount: 0,

                            // Mapped properties
                            status: 'Processing',
                            views: 0,
                            likes: '--',
                            comments: 0,
                            date: new Date().toISOString(),
                            thumbnail: 'assets/placeholder.jpg',
                            visibility: 'Public',
                            progress: 0
                        });
                    }

                    this.videos.set(fetchedVideos);
                }
            },
            error: (err) => console.error('Error fetching videos', err)
        });
    }

    ngOnDestroy() {
        if (this.pollInterval) {
            clearInterval(this.pollInterval);
        }
    }

    startPolling() {
        // Poll every 5 seconds
        this.pollInterval = setInterval(() => {
            const currentVideos = this.videos();
            // console.log('DEBUG: Polling tick. Videos count:', currentVideos.length);

            currentVideos.forEach((video, index) => {
                // Only poll for videos with valid UUIDs and not in final state
                if (video.id.length > 5 && video.status !== 'Uploaded' && video.status !== 'COMPLETED' && video.status !== 'FAILED') {
                    console.log(`DEBUG: Polling status for ${video.id} (Status: ${video.status})`);
                    this.videoService.getVideoStatus(video.id).subscribe({
                        next: (response) => {
                            if (response.success && response.data) {
                                this.updateVideoStatus(index, response.data);
                            }
                        },
                        error: (err) => console.error('Polling error', err)
                    });
                }
            });
        }, 5000);
    }

    updateVideoStatus(index: number, data: any) {
        this.videos.update(currentVideos => {
            const updated = [...currentVideos];
            const video = { ...updated[index] };

            // Map API status to UI status
            video.status = data.status === 'COMPLETED' ? 'Uploaded' : (data.status === 'FAILED' ? 'Failed' : 'Processing');
            video.progress = data.progress || 0;
            video.title = data.title; // Update incase title changed

            // If completed, update thumbnail
            if (data.status === 'COMPLETED') {
                video.thumbnail = data.thumbnailUrl ?
                    (data.thumbnailUrl.startsWith('http') ? data.thumbnailUrl : `http://localhost:1205/api/v1/stream/${data.id}/thumbnail.jpg`)
                    : video.thumbnail;
            }

            updated[index] = video;
            return updated;
        });
    }

    formatDate(dateStr: string) {
        return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }
}
