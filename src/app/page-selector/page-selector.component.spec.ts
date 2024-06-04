import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageSelectorComponent } from './page-selector.component';

describe('PageSelectorComponent', () => {
  let component: PageSelectorComponent;
  let fixture: ComponentFixture<PageSelectorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PageSelectorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PageSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
