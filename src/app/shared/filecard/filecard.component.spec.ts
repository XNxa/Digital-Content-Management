import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilecardComponent } from './filecard.component';

describe('FilecardComponent', () => {
  let component: FilecardComponent;
  let fixture: ComponentFixture<FilecardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilecardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FilecardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
