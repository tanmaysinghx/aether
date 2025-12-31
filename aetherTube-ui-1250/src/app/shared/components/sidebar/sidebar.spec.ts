import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Sidebar } from './sidebar';

describe('Sidebar', () => {
    let component: Sidebar;
    let fixture: ComponentFixture<Sidebar>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [Sidebar]
        })
            .compileComponents();

        fixture = TestBed.createComponent(Sidebar);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should render a nav element', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const nav = compiled.querySelector('nav');
        expect(nav).toBeTruthy();
    });

    it('should contain main sections (Home, Trending, Subscriptions)', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const text = compiled.textContent || '';
        expect(text).toContain('Home');
        expect(text).toContain('Trending');
        expect(text).toContain('Subscriptions');
    });

    it('should display Ae logo with tube text', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const logoLink = compiled.querySelector('a[aria-label="Ae tube"]');
        expect(logoLink).toBeTruthy();
        const logoText = logoLink?.textContent?.trim();
        expect(logoText).toContain('tube');
    });
});
