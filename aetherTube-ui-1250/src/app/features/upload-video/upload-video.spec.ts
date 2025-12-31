import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadVideo } from './upload-video';

describe('UploadVideo', () => {
  let component: UploadVideo;
  let fixture: ComponentFixture<UploadVideo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadVideo]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadVideo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
