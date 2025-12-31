import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Header } from './header';
import { UiService } from '../../services/ui.service';

describe('Header', () => {
    let component: Header;
    let fixture: ComponentFixture<Header>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [Header]
        })
            .compileComponents();

        fixture = TestBed.createComponent(Header);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should render the header element', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const header = compiled.querySelector('header');
        expect(header).toBeTruthy();
    });

    it('should display tube branding', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const brandText = compiled.textContent;
        expect(brandText).toContain('tube');
    });

    it('should have search input', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const searchInput = compiled.querySelector('input[type="text"]#search');
        expect(searchInput).toBeTruthy();
    });

    it('should have mobile sidebar toggle button', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const toggleButton = compiled.querySelector('button[aria-label="Toggle sidebar"]');
        expect(toggleButton).toBeTruthy();
    });

    it('should toggle the sidebar when mobile toggle clicked', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const toggleButton = compiled.querySelector('button[aria-label="Toggle sidebar"]') as HTMLButtonElement;
        const ui = TestBed.inject(UiService);

        expect(ui.sidebarOpen()).toBeFalse();
        toggleButton.click();
        fixture.detectChanges();
        expect(ui.sidebarOpen()).toBeTrue();

        toggleButton.click();
        fixture.detectChanges();
        expect(ui.sidebarOpen()).toBeFalse();
    });

    it('should have notification button', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const buttons = compiled.querySelectorAll('button[type="button"]');
        // Should have at least 2 buttons (sidebar toggle + notification)
        expect(buttons.length).toBeGreaterThanOrEqual(2);
    });

    it('should have user avatar image', () => {
        const compiled = fixture.nativeElement as HTMLElement;
        const avatar = compiled.querySelector('img.rounded-full');
        expect(avatar).toBeTruthy();
    });
});
