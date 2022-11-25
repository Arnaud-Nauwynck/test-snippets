import { TestBed } from '@angular/core/testing';

import { RentServiceService } from './rent-service.service';

describe('RentServiceService', () => {
  let service: RentServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RentServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
