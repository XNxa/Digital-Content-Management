import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LongInputComponent } from './long-input.component';

describe('LongInputComponent', () => {
  let component: LongInputComponent;
  let fixture: ComponentFixture<LongInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LongInputComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LongInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
