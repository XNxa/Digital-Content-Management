import { Component } from '@angular/core';
import { IconButtonComponent } from "../../shared/components/buttons/icon-button/icon-button.component";
import { SmallCardComponent } from "../../shared/components/small-card/small-card.component";
import { of } from 'rxjs';
import { FilesStateComponent } from "./files-state/files-state.component";

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [IconButtonComponent, SmallCardComponent, FilesStateComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css'
})
export class HomePageComponent {

  a = () => of(1);

}
