import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { AuthService } from './shared/auth-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

    login: string = 'user';
    password: string = 'password';
    
    constructor(private authService: AuthService) {
    }

    onClickLogin() {
        this.authService.login(this.login, this.password);
    }
}
