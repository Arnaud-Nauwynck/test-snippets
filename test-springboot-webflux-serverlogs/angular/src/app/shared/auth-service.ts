import { Injectable } from '@angular/core';

@Injectable()
export class AuthService {
 
    username: string = 'user';
    password: string = 'password';

    login(username: string, password: string) {
        this.username = username;
        this.password = password;
    }
    
    getAuthorizationToken(): string {
        return 'Basic dXNlcjpwYXNzd29yZA=='; // base64(user:password)   
    }
}