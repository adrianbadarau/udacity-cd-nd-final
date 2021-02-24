import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { KListTestModule } from '../../../test.module';
import { RecipeComponent } from 'app/entities/recipe/recipe.component';
import { RecipeService } from 'app/entities/recipe/recipe.service';
import { Recipe } from 'app/shared/model/recipe.model';

describe('Component Tests', () => {
  describe('Recipe Management Component', () => {
    let comp: RecipeComponent;
    let fixture: ComponentFixture<RecipeComponent>;
    let service: RecipeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [KListTestModule],
        declarations: [RecipeComponent],
      })
        .overrideTemplate(RecipeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RecipeComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RecipeService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Recipe('123')],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.recipes && comp.recipes[0]).toEqual(jasmine.objectContaining({ id: '123' }));
    });
  });
});
