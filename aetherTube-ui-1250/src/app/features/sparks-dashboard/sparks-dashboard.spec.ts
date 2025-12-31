import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SparksDashboard } from './sparks-dashboard';

describe('SparksDashboard', () => {
  let component: SparksDashboard;
  let fixture: ComponentFixture<SparksDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SparksDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SparksDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
