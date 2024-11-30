import {Routes} from '@angular/router';
import {CitiesComponent} from './cities/cities.component';
import {StreetNamesComponent} from './street-names/street-names.component';
import {AddressesComponent} from './addresses/addresses.component';
import {FirstNamesComponent} from './firstnames/first-names.component';
import {StreetsComponent} from './streets/streets.component';

export const routes: Routes = [
  {path: 'cities', component: CitiesComponent},
  {path: 'street-names', component: StreetNamesComponent},
  {path: 'streets', component: StreetsComponent},
  {path: 'addresses', component: AddressesComponent},
  {path: 'first-names', component: FirstNamesComponent},
];
