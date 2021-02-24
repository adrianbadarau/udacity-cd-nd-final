import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IList, List } from 'app/shared/model/list.model';
import { ListService } from './list.service';

@Component({
  selector: 'jhi-list-update',
  templateUrl: './list-update.component.html',
})
export class ListUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
  });

  constructor(protected listService: ListService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ list }) => {
      this.updateForm(list);
    });
  }

  updateForm(list: IList): void {
    this.editForm.patchValue({
      id: list.id,
      name: list.name,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const list = this.createFromForm();
    if (list.id !== undefined) {
      this.subscribeToSaveResponse(this.listService.update(list));
    } else {
      this.subscribeToSaveResponse(this.listService.create(list));
    }
  }

  private createFromForm(): IList {
    return {
      ...new List(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IList>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
