import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IList } from 'app/shared/model/list.model';
import { ListService } from './list.service';
import { ListDeleteDialogComponent } from './list-delete-dialog.component';

@Component({
  selector: 'jhi-list',
  templateUrl: './list.component.html',
})
export class ListComponent implements OnInit, OnDestroy {
  lists?: IList[];
  eventSubscriber?: Subscription;

  constructor(protected listService: ListService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.listService.query().subscribe((res: HttpResponse<IList[]>) => (this.lists = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInLists();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IList): string {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInLists(): void {
    this.eventSubscriber = this.eventManager.subscribe('listListModification', () => this.loadAll());
  }

  delete(list: IList): void {
    const modalRef = this.modalService.open(ListDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.list = list;
  }
}
