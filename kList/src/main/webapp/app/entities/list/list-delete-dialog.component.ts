import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IList } from 'app/shared/model/list.model';
import { ListService } from './list.service';

@Component({
  templateUrl: './list-delete-dialog.component.html',
})
export class ListDeleteDialogComponent {
  list?: IList;

  constructor(protected listService: ListService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.listService.delete(id).subscribe(() => {
      this.eventManager.broadcast('listListModification');
      this.activeModal.close();
    });
  }
}
