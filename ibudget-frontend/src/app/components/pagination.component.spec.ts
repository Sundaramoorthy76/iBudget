import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  it('calculates page count and visible item range', () => {
    const component = new PaginationComponent();
    component.totalItems = 26;
    component.pageIndex = 1;
    component.pageSize = 10;

    expect(component.totalPages).toBe(3);
    expect(component.firstItem).toBe(11);
    expect(component.lastItem).toBe(20);
  });

  it('reports an empty range when there are no items', () => {
    const component = new PaginationComponent();

    expect(component.firstItem).toBe(0);
    expect(component.lastItem).toBe(0);
  });

  it('emits the selected page size and resets to the first page', () => {
    const component = new PaginationComponent();
    const changedPageSizes: number[] = [];
    const changedPages: number[] = [];
    component.pageSizeChange.subscribe((size) => changedPageSizes.push(size));
    component.pageIndexChange.subscribe((index) => changedPages.push(index));

    component.changePageSize(25);

    expect(changedPageSizes).toEqual([25]);
    expect(changedPages).toEqual([0]);
  });
});
