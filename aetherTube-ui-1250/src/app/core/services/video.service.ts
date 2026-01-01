import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface VideoFeedItem {
    id: string;
    title: string;
    thumbnailUrl: string;
    viewCount?: number;
    durationSeconds: number;
    uploaderId: string;
    videoType: string;
    createdAt: string;
    appSource: string;
}

export interface VideoDetails extends VideoFeedItem {
    description: string;
    visibility: string;
    language: string;
    tags: string[];
    category: string;
    status: string;
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

    // Initialize signal from localStorage to persist state across refreshes
    lastUploadedVideoId = signal<string | null>(localStorage.getItem('lastUploadedVideoId'));

    setLastUploadedVideoId(id: string) {
        localStorage.setItem('lastUploadedVideoId', id);
        this.lastUploadedVideoId.set(id);
    }

    clearLastUploadedVideoId() {
        localStorage.removeItem('lastUploadedVideoId');
        this.lastUploadedVideoId.set(null);
    }

    getFeed(appSource: string = 'TUBE'): Observable<ApiResponse<VideoFeedItem[]>> {
        return this.http.get<ApiResponse<VideoFeedItem[]>>(`${this.apiUrl}/media/feed?appSource=${appSource}`);
    }

    getVideo(id: string): Observable<ApiResponse<VideoDetails>> {
        return this.http.get<ApiResponse<VideoDetails>>(`${this.apiUrl}/media/${id}`);
    }

    getMyVideos(): Observable<ApiResponse<VideoFeedItem[]>> {
        // Fetching feed but filtering for my videos (MOCK User ID)
        // In a real app, this would likely be a distinct endpoint like /media/my-videos
        return this.http.get<ApiResponse<VideoFeedItem[]>>(`${this.apiUrl}/media/feed?appSource=TUBE&uploaderId=550e8400-e29b-41d4-a716-446655440000`);
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
    uploadVideo(data: {
        file: File;
        title: string;
        description: string;
        visibility: string;
        videoType: string;
        language: string;
        tags: string[];
        category: string;
    }): Observable<HttpEvent<any>> {
        const formData = new FormData();
        formData.append('file', data.file);
        formData.append('title', data.title);
        formData.append('description', data.description);
        formData.append('appSource', 'TUBE');
        formData.append('visibility', data.visibility);
        formData.append('videoType', data.videoType);
        formData.append('language', data.language);
        formData.append('metadata', JSON.stringify({
            tags: data.tags,
            category: data.category
        }));

        const headers = {
            'X-User-Id': '550e8400-e29b-41d4-a716-446655440000', // Mock User
            'X-App-Id': 'TUBE'
        };

        return this.http.post<any>(`${this.apiUrl}/media/upload`, formData, {
            headers,
            reportProgress: true,
            observe: 'events'
        });
    }
    getVideoStatus(videoId: string): Observable<ApiResponse<any>> {
        const headers = {
            'X-User-Id': '550e8400-e29b-41d4-a716-446655440000',
            'X-App-Id': 'TUBE'
        };
        return this.http.get<ApiResponse<any>>(`${this.apiUrl}/media/${videoId}`, { headers });
    }
}
