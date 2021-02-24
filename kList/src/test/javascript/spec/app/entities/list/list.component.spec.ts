import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { KListTestModule } from '../../../test.module';
import { ListComponent } from 'app/entities/list/list.component';
import { ListService } from 'app/entities/list/list.service';
import { List } from 'app/shared/model/list.model';

describe('Component Tests', () => {
  describe('List Management Component', () => {
    let comp: ListComponent;
    let fixture: ComponentFixture<ListComponent>;
    let service: ListService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [KListTestModule],
        declarations: [ListComponent],
      })
        .overrideTemplate(ListComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ListComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ListService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new List('123')],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.lists && comp.lists[0]).toEqual(jasmine.objectContaining({ id: '123' }));
    });
  });
});
