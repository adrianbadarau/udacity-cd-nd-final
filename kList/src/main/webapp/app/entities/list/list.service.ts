import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IList } from 'app/shared/model/list.model';

type EntityResponseType = HttpResponse<IList>;
type EntityArrayResponseType = HttpResponse<IList[]>;

@Injectable({ providedIn: 'root' })
export class ListService {
  public resourceUrl = SERVER_API_URL + 'api/lists';

  constructor(protected http: HttpClient) {}

  create(list: IList): Observable<EntityResponseType> {
    return this.http.post<IList>(this.resourceUrl, list, { observe: 'response' });
  }

  update(list: IList): Observable<EntityResponseType> {
    return this.http.put<IList>(this.resourceUrl, list, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IList>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IList[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
