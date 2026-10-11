import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
  standalone: false
})
export class PaginationComponent {
  @Input() totalItems = 0;
  @Input() pageIndex = 0;
  @Input() pageSize = 10;
  @Output() pageIndexChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.totalItems / this.pageSize));
  }

  get firstItem(): number {
    return this.totalItems === 0 ? 0 : this.pageIndex * this.pageSize + 1;
  }

  get lastItem(): number {
    return Math.min((this.pageIndex + 1) * this.pageSize, this.totalItems);
  }

  goToPage(index: number): void {
    if (index >= 0 && index < this.totalPages && index !== this.pageIndex) {
      this.pageIndexChange.emit(index);
    }
  }

  changePageSize(size: number): void {
    this.pageSizeChange.emit(size);
    this.pageIndexChange.emit(0);
  }
}
