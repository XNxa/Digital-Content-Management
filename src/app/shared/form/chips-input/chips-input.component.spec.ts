import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChipsInputComponent } from './chips-input.component';

describe('ChipsInputComponent', () => {
  let component: ChipsInputComponent;
  let fixture: ComponentFixture<ChipsInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChipsInputComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChipsInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
