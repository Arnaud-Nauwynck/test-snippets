import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient} from '@angular/common/http';

import {ApiModule, Configuration, ConfigurationParameters} from './generated/rest-bal';

const apiConfParams : ConfigurationParameters = {
  basePath: 'http://localhost:4200' // override generated code
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    importProvidersFrom(ApiModule.forRoot(() => new Configuration(apiConfParams)))
  ]
};
