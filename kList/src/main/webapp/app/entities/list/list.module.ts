import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { KListSharedModule } from 'app/shared/shared.module';
import { ListComponent } from './list.component';
import { ListDetailComponent } from './list-detail.component';
import { ListUpdateComponent } from './list-update.component';
import { ListDeleteDialogComponent } from './list-delete-dialog.component';
import { listRoute } from './list.route';

@NgModule({
  imports: [KListSharedModule, RouterModule.forChild(listRoute)],
  declarations: [ListComponent, ListDetailComponent, ListUpdateComponent, ListDeleteDialogComponent],
  entryComponents: [ListDeleteDialogComponent],
})
export class KListListModule {}
