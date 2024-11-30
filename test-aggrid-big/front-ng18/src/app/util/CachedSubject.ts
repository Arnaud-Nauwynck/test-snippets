import {Observable, Subject, Subscriber, Subscription} from 'rxjs';

export class CachedSubject<T> extends Subject<T> {
  private _value: T | undefined;
  private _hasEmitted: boolean = false;  // Track if the first value was emitted
  private _cacheLoader: () => Observable<T>;

  constructor(cacheLoader: () => Observable<T>) {
    super();
    this._cacheLoader = cacheLoader;
    this._value = undefined;
    this.loadCache();
  }

  private loadCache() {
    this._cacheLoader().subscribe({
      next: value => {
        this._value = value;
        this.next(value);
      },
      error: (err) => {
        // TODO need retry later ..
        this.error(err)
      },
      complete: () => {
        // ??
      }
    });
  }

  // @ts-ignore
  public override subscribe(subscriber: Subscriber<T>): Subscription {
    const subscription = super.subscribe(subscriber);
    if (this._hasEmitted && !subscription.closed && this._value !== undefined) {
      subscriber.next(this._value);  // Only emit if a value has been emitted before
    }
    return subscription;
  }

  public override next(value: T): void {
    this._value = value;
    this._hasEmitted = true;  // Set to true after the first emit
    super.next(value);
  }
}
