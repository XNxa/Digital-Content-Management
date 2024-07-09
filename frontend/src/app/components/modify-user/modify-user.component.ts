import { Component, OnInit } from '@angular/core';
import { InputComponent } from "../../shared/components/form/input/input.component";
import { ToggleButtonComponent } from "../../shared/components/buttons/toggle-button/toggle-button.component";
import { LongInputComponent } from "../../shared/components/form/long-input/long-input.component";
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { UserApiService } from '../../services/user-api.service';
import { RoleApiService } from '../../services/role-api.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User } from '../../models/User';
import { SelectComponent } from "../../shared/components/form/select/select.component";

@Component({
  selector: 'app-modify-user',
  standalone: true,
  imports: [InputComponent, ToggleButtonComponent, LongInputComponent, IconTextButtonComponent, RouterModule, SelectComponent],
  templateUrl: './modify-user.component.html',
  styleUrl: './modify-user.component.css'
})
export class ModifyUserComponent implements OnInit {

  mode: 'modify' | 'consult' = 'consult';

  user!: User;

  firstname = new FormControl('', [Validators.required]);
  lastname = new FormControl('', [Validators.required]);
  function = new FormControl('', [Validators.required]);
  email = new FormControl('', [Validators.required, Validators.email]);

  role = new FormControl('', [Validators.required]);
  state: boolean = false;

  password = new FormControl('', [Validators.required]);
  passwordConfirmation = new FormControl('', [Validators.required]);

  group1 = new FormGroup({ firstname: this.firstname, lastname: this.lastname, function: this.function, email: this.email });
  group2 = new FormGroup({ role: this.role });
  group3 = new FormGroup({ password: this.password, passwordConfirmation: this.passwordConfirmation }, {
    validators: this.passwordsMatchValidator()
  });

  roleOptions!: string[];

  constructor(private userapi: UserApiService, private roleapi: RoleApiService, private route: ActivatedRoute, private router: Router) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.userapi.getUser(params['id']).subscribe(user => {
        this.user = user;
        this.firstname.setValue(user.firstname);
        this.lastname.setValue(user.lastname);
        this.function.setValue(user.function);
        this.email.setValue(user.email);
        this.role.setValue(user.role);
        this.state = user.statut == 'active';
      });
    });
    this.roleapi.getActiveRoles().subscribe(roles => {
      this.roleOptions = roles;
    });
  }

  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('password');
      const passwordConfirmation = control.get('passwordConfirmation');

      if (password && passwordConfirmation && password.value != passwordConfirmation.value) {
        return { passwordMismatch: true };
      }
      return null;
    };
  }

  deleteUser() {
    this.userapi.deleteUser(this.user.email).subscribe(() => {
      this.router.navigate(['/users']);
    });
  }

  modify() {
    this.mode = 'modify';
  }

  cancel() {
    this.firstname.setValue(this.user.firstname);
    this.lastname.setValue(this.user.lastname);
    this.function.setValue(this.user.function);
    this.email.setValue(this.user.email);
    this.role.setValue(this.user.role);
    this.state = this.user.statut == 'active';
    this.mode = 'consult';
  }

  save() {
    if (this.group1.valid && this.group2.valid && this.group3.valid) {
      const user: User = {
        id: this.user.id,
        firstname: this.firstname.value!,
        lastname: this.lastname.value!,
        function: this.function.value!,
        email: this.email.value!,
        role: this.role.value!,
        statut: this.state ? 'active' : 'inactive',
        password: this.password.value!
      }
  
      this.userapi.updateUser(user).subscribe(() => {
        this.mode = 'consult';
      });
    } else {
      this.group1.markAllAsTouched();
      this.group2.markAllAsTouched();
      this.group3.markAllAsTouched();
    }
  }
}
