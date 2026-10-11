import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { BudgetEntry, ViewName } from '../models';

@Component({
  selector: 'app-transactions-screen',
  templateUrl: './transactions-screen.component.html',
  standalone: false
})
export class TransactionsScreenComponent implements OnChanges {
  @ViewChild('firstEditable') firstEditable?: ElementRef<HTMLInputElement>;
  pageIndex = 0;
  pageSize = 10;

  @Input() view: 'income' | 'expense' = 'expense';
  @Input() entries: BudgetEntry[] = [];
  @Input() form!: BudgetEntry;
  @Input() readOnly = false;
  @Input() editing = false;
  @Output() submitted = new EventEmitter<void>();
  @Output() viewed = new EventEmitter<BudgetEntry>();
  @Output() edited = new EventEmitter<BudgetEntry>();
  @Output() deleted = new EventEmitter<number | undefined>();
  @Output() cancelled = new EventEmitter<void>();

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['view']) {
      this.pageIndex = 0;
    }
    this.keepPageInRange();
    if (changes['readOnly'] || changes['editing'] || changes['view']) {
      this.focusFirstEditable();
    }
  }

  private focusFirstEditable(): void {
    if (this.readOnly) {
      return;
    }
    setTimeout(() => this.firstEditable?.nativeElement.focus());
  }

  get title(): string {
    const label = this.view === 'income' ? 'income' : 'expense';
    return this.readOnly ? `View ${label}` : this.editing ? `Edit ${label}` : `Add ${label}`;
  }

  get filteredEntries(): BudgetEntry[] {
    return this.entries.filter((entry) => entry.type === this.view.toUpperCase());
  }

  get visibleEntries(): BudgetEntry[] {
    const start = this.pageIndex * this.pageSize;
    return this.filteredEntries.slice(start, start + this.pageSize);
  }

  changePage(index: number): void {
    this.pageIndex = index;
  }

  changePageSize(size: number): void {
    this.pageSize = size;
    this.pageIndex = 0;
  }

  private keepPageInRange(): void {
    const lastPage = Math.max(0, Math.ceil(this.filteredEntries.length / this.pageSize) - 1);
    this.pageIndex = Math.min(this.pageIndex, lastPage);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
  }
}
