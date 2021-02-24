import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { KListTestModule } from '../../../test.module';
import { ListUpdateComponent } from 'app/entities/list/list-update.component';
import { ListService } from 'app/entities/list/list.service';
import { List } from 'app/shared/model/list.model';

describe('Component Tests', () => {
  describe('List Management Update Component', () => {
    let comp: ListUpdateComponent;
    let fixture: ComponentFixture<ListUpdateComponent>;
    let service: ListService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [KListTestModule],
        declarations: [ListUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(ListUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ListUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ListService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new List('123');
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new List();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
