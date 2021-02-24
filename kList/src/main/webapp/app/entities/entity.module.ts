import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'list',
        loadChildren: () => import('./list/list.module').then(m => m.KListListModule),
      },
      {
        path: 'item',
        loadChildren: () => import('./item/item.module').then(m => m.KListItemModule),
      },
      {
        path: 'recipe',
        loadChildren: () => import('./recipe/recipe.module').then(m => m.KListRecipeModule),
      },
      {
        path: 'notification',
        loadChildren: () => import('./notification/notification.module').then(m => m.KListNotificationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class KListEntityModule {}
