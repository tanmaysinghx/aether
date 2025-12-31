import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface VideoFeedItem {
    id: string;
    title: string;
    thumbnailUrl: string;
    durationSeconds: number;
    uploaderId: string;
    videoType: string;
    views?: number; // Optional as it might not be in feed yet
    createdAt: string;
    appSource: string;
}

export interface ApiResponse<T> {
    code: string;
    data: T;
    message: string;
    success: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class VideoService {
    private http = inject(HttpClient);
    private apiUrl = 'http://localhost:1205/api/v1';

    getFeed(): Observable<ApiResponse<VideoFeedItem[]>> {
        return this.http.get<ApiResponse<VideoFeedItem[]>>(`${this.apiUrl}/media/feed?appSource=TUBE`);
    }

    getVideoStream(videoId: string): Observable<string> {
        // In the future, this could fetch a signed URL or check permissions
        return new Observable(observer => {
            observer.next(`http://localhost:1205/api/v1/stream/${videoId}/master.m3u8`);
            observer.complete();
        });
    }

    saveProgress(videoId: string, progressSeconds: number): Observable<any> {
        const headers = {
            'X-User-Id': '550e8400-e29b-41d4-a716-446655440000', // Mock User
            'X-App-Id': 'TUBE'
        };
        return this.http.post(`${this.apiUrl}/play/progress`, { videoId, progressSeconds }, { headers });
    }

    getProgress(videoId: string): Observable<any> {
        const headers = {
            'X-User-Id': '550e8400-e29b-41d4-a716-446655440000', // Mock User
            'X-App-Id': 'TUBE'
        };
        return this.http.get(`${this.apiUrl}/play/progress/${videoId}`, { headers });
    }
}
