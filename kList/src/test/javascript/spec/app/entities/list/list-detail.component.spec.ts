import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { KListTestModule } from '../../../test.module';
import { ListDetailComponent } from 'app/entities/list/list-detail.component';
import { List } from 'app/shared/model/list.model';

describe('Component Tests', () => {
  describe('List Management Detail Component', () => {
    let comp: ListDetailComponent;
    let fixture: ComponentFixture<ListDetailComponent>;
    const route = ({ data: of({ list: new List('123') }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [KListTestModule],
        declarations: [ListDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ListDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ListDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load list on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.list).toEqual(jasmine.objectContaining({ id: '123' }));
      });
    });
  });
});
