import { Component, signal, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VideoService } from '../../../core/services/video.service';
import { HttpEventType } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload-video',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './upload-video.html',
  styleUrls: ['./upload-video.scss']
})
export class UploadVideo implements OnDestroy {
  private videoService = inject(VideoService);
  private router = inject(Router);
  private pollInterval: any;

  // Steps: 1 = Upload, 2 = Details, 3 = Review/Finish
  currentStep = signal(1);

  isDragging = signal(false);
  file = signal<File | null>(null);
  uploadProgress = signal(0);
  processingProgress = signal(0);
  uploadStatus = signal<'idle' | 'uploading' | 'processing' | 'success' | 'error'>('idle');
  uploadedVideoId = signal<string | null>(null);

  // Data Signals
  title = signal('');
  description = signal('');
  visibility = signal('PUBLIC'); // PUBLIC, PRIVATE, UNLISTED
  category = signal('Education');
  tags = signal<string[]>([]);
  language = signal('en');

  ngOnDestroy() {
    if (this.pollInterval) {
      clearInterval(this.pollInterval);
    }
  }


  // Helper for tag input
  currentTagInput = signal('');

  categories = [
    'Gaming', 'Music', 'Education', 'Science & Tech',
    'Entertainment', 'Comedy', 'News', 'Sports'
  ];

  resolutions = ['360p', '480p', '720p', '1080p', '4K'];
  selectedResolutions = signal<string[]>(['720p', '1080p']); // Default selection

  toggleResolution(res: string) {
    this.selectedResolutions.update(current => {
      if (current.includes(res)) {
        return current.filter(r => r !== res);
      } else {
        return [...current, res];
      }
    });
  }

  /* --- Step 1: Drag & Drop --- */

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(true);
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging.set(false);

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.handleFile(input.files[0]);
    }
  }

  handleFile(file: File) {
    if (file.type.startsWith('video/')) {
      this.file.set(file);
      // Auto-fill title from filename
      this.title.set(file.name.replace(/\.[^/.]+$/, ""));
      // Move to next step
      this.currentStep.set(2);
    } else {
      alert('Please select a valid video file.');
    }
  }

  /* --- Step 2: Form Handling --- */

  updateTitle(event: Event) {
    this.title.set((event.target as HTMLInputElement).value);
  }

  updateDescription(event: Event) {
    this.description.set((event.target as HTMLTextAreaElement).value);
  }

  addTag(event: Event) {
    const input = event.target as HTMLInputElement;
    const value = input.value.trim();
    if (value && !this.tags().includes(value)) {
      this.tags.update(tags => [...tags, value]);
      input.value = '';
    }
  }

  removeTag(tagToRemove: string) {
    this.tags.update(tags => tags.filter(t => t !== tagToRemove));
  }

  /* --- Navigation --- */

  nextStep() {
    if (this.currentStep() < 3) {
      this.currentStep.update(s => s + 1);
    }
  }

  prevStep() {
    if (this.currentStep() > 1) {
      this.currentStep.update(s => s - 1);
    }
  }


  /* --- Upload Logic --- */

  upload() {
    if (!this.file() || this.uploadStatus() === 'uploading') return;

    this.uploadStatus.set('uploading');
    this.uploadProgress.set(0);
    this.processingProgress.set(0);

    const payload = {
      file: this.file()!,
      title: this.title(),
      description: this.description(),
      visibility: this.visibility(),
      videoType: 'EPISODE',
      language: this.language(),
      tags: this.tags(),
      category: this.category()
    };

    this.videoService.uploadVideo(payload)
      .subscribe({
        next: (event) => {
          if (event.type === HttpEventType.UploadProgress && event.total) {
            this.uploadProgress.set(Math.round(100 * event.loaded / event.total));
          } else if (event.type === HttpEventType.Response) {
            const response = event.body as any;
            if (response.success && response.data && response.data.id) {
              // Redirect to content page immediately
              this.videoService.setLastUploadedVideoId(response.data.id);
              this.router.navigate(['/studio/content']);
            } else {
              this.uploadStatus.set('success');
            }
          }
        },
        error: (err) => {
          console.error(err);
          this.uploadStatus.set('error');
        }
      });
  }

  startPolling(videoId: string) {
    this.pollInterval = setInterval(() => {
      this.videoService.getVideoStatus(videoId).subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.processingProgress.set(response.data.progress || 0);

            if (response.data.status === 'COMPLETED') {
              this.uploadStatus.set('success');
              clearInterval(this.pollInterval);
            } else if (response.data.status === 'FAILED') {
              this.uploadStatus.set('error');
              clearInterval(this.pollInterval);
            }
          }
        },
        error: (err) => console.error('Polling error', err)
      });
    }, 2000);
  }

  cancel() {
    if (this.pollInterval) clearInterval(this.pollInterval);
    this.file.set(null);
    this.uploadStatus.set('idle');
    this.uploadProgress.set(0);
    this.processingProgress.set(0);
    this.title.set('');
    this.description.set('');
    this.currentStep.set(1);
    this.tags.set([]);
    this.uploadedVideoId.set(null);
  }
}
