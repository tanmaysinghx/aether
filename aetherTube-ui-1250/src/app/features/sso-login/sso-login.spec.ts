import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SsoLogin } from './sso-login';

describe('SsoLogin', () => {
  let component: SsoLogin;
  let fixture: ComponentFixture<SsoLogin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SsoLogin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SsoLogin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
