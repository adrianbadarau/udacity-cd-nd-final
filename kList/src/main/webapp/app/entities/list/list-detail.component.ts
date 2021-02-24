import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IList } from 'app/shared/model/list.model';

@Component({
  selector: 'jhi-list-detail',
  templateUrl: './list-detail.component.html',
})
export class ListDetailComponent implements OnInit {
  list: IList | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ list }) => (this.list = list));
  }

  previousState(): void {
    window.history.back();
  }
}
