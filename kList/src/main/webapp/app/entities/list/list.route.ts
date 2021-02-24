import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IList, List } from 'app/shared/model/list.model';
import { ListService } from './list.service';
import { ListComponent } from './list.component';
import { ListDetailComponent } from './list-detail.component';
import { ListUpdateComponent } from './list-update.component';

@Injectable({ providedIn: 'root' })
export class ListResolve implements Resolve<IList> {
  constructor(private service: ListService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IList> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((list: HttpResponse<List>) => {
          if (list.body) {
            return of(list.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new List());
  }
}

export const listRoute: Routes = [
  {
    path: '',
    component: ListComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'kListApp.list.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ListDetailComponent,
    resolve: {
      list: ListResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'kListApp.list.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ListUpdateComponent,
    resolve: {
      list: ListResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'kListApp.list.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ListUpdateComponent,
    resolve: {
      list: ListResolve,
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'kListApp.list.home.title',
    },
    canActivate: [UserRouteAccessService],
  },
];
