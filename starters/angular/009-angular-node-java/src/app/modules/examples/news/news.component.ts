import {
  Component,
  OnInit,
  inject,
  signal
} from '@angular/core';

import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder
} from '@angular/forms';

import { Router } from '@angular/router';

import { finalize } from 'rxjs';

import { environment } from '../../../../environments/environment';

import { Item } from './items/item';
import { Badge } from './badge';

import { ItemsService } from './items/items.service';

import { CategoryPipe } from './category.pipe';
import { SafePipe } from './safe.pipe';

declare const bootstrap: any;

@Component({
  selector: 'app-news',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CategoryPipe,
    SafePipe
  ],
  templateUrl: './news.component.html',
  styleUrl: './news.component.css'
})
export class NewsComponent implements OnInit {

  readonly items = signal<Item[]>([]);
  readonly loaded = signal(false);
  readonly playerLoaded = signal(false);

  searchField = '';
  player = '';

  badges: Badge[] = [];

  private modalPlayer: any;

  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly itemsService = inject(ItemsService);

  readonly formFilters = this.fb.group({
    dateType: [1],
    fromDate: [''],
    toDate: [''],
    sortType: [1],
    show: [false],
    movie: [false],
    clip: [false],
    game: [false],
    elementsCount: [20]
  });

  ngOnInit(): void {
    this.getItems();
  }

  getItems(): void {

    this.loaded.set(false);

    this.itemsService
      .getItems(environment.urlNews)
      .pipe(
        finalize(() => this.loaded.set(true))
      )
      .subscribe(items => {
        this.items.set(items);
      });

  }

  onSearch(): void {
    this.getItems();
  }

  onHandleKeyDown(event: KeyboardEvent): void {

    if (event.key === 'Enter') {
      this.onSearch();
    }

  }

  openTrailer(item: Item): void {

    this.player = item.youtubeLink;
    this.playerLoaded.set(true);

    if (!this.modalPlayer) {

      this.modalPlayer = new bootstrap.Modal(
        document.getElementById('newsModal'),
        {
          keyboard: true
        }
      );

      document
        .getElementById('newsModal')
        ?.addEventListener(
          'hidden.bs.modal',
          this.onCloseModal.bind(this)
        );

    }

    this.modalPlayer.show();

  }

  onCloseModal(): void {

    this.player = '';
    this.playerLoaded.set(false);

  }

}


// import { Component, OnInit } from '@angular/core';
// import { Router } from '@angular/router';
// import { FormBuilder, FormGroup } from '@angular/forms';

// import { Item } from './items/item';
// import { Badge } from './badge';
// import { ItemsService } from './items/items.service';
// import { environment } from '../../../../environments/environment';

// import { CategoryPipe } from './category.pipe';
// import { SafePipe } from './safe.pipe';

// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ReactiveFormsModule } from '@angular/forms';


// declare const bootstrap: any;

// @Component({
//   selector: 'app-news',
//   imports: [
//     CategoryPipe,
//     SafePipe,
//     CommonModule,
//     FormsModule,
//     ReactiveFormsModule,
//   ],
//   templateUrl: './news.component.html',
//   styleUrls: ['./news.component.css']
// })
// export class NewsComponent implements OnInit {

//   itemsLoaded: boolean;
//   items: Item[];
//   badges: Badge[];
//   searchField: string;
//   player: string;
//   playerLoaded: boolean;
//   modalPlayer: any;
//   loaded: boolean;
//   filtersEnabled: boolean;
//   resultsFound: boolean;

//   formFilters!: FormGroup;

//   constructor(
//     public router: Router,
//     private itemsService: ItemsService,
//     private fb: FormBuilder) {
    
//     this.formFilters = this.fb.group({
//       dateType: [1],
//       fromDate: [''],
//       toDate: [''],
//       sortType: [1],
//       show: [false],
//       movie: [false],
//       clip: [false],
//       game: [false],
//       elementsCount: [0],
//     });

//     this.loaded = false;
//     this.items = [];
//     this.badges = [];
//     this.itemsLoaded = false;
//     this.searchField = ''
//     this.player = '';
//     this.playerLoaded = false;
//     this.filtersEnabled = false;
//     this.resultsFound = false;

//     this.formFilters.setValue({
//       dateType: 1,
//       fromDate: '',
//       toDate: '',
//       sortType: 1,
//       show: false,
//       movie: false,
//       clip: false,
//       game: false,
//       elementsCount: 20,
//     });

//   }

//   ngOnInit(): void {
//     this.getItems();
//   }

//   getItems() {
//     console.log('00000000001:getItems');
//     this.loaded = false;
//     const url = environment.urlNews;
//     this.itemsService.getItems(url)
//       .subscribe(
//         items => {
//           console.log('00000000001:getItems:' + JSON.stringify(items));
//           this.items = items;
//           this.loaded = true;
//         }
//       );
//   }

//   openTrailer(item: any) {
//     this.player = item.youtubeLink;
//     this.playerLoaded = true;
//     if (this.modalPlayer === undefined) {
//       this.modalPlayer = new bootstrap.Modal(document.getElementById('newsModal'), {
//         keyboard: true
//       })
//       const selectPlayer = document.getElementById('newsModal')
//       selectPlayer?.addEventListener('hidden.bs.modal', this.onCloseModal.bind(this));
//     }
//     this.modalPlayer?.show();
//   }

//   onCloseModal() {
//     this.player = '';
//     this.playerLoaded = false;
//   }

//   onHandleKeyDown(event: any) {
//     if (event.keyCode === 13) {
//       this.onSearch();
//     }
//   }

//   onSearch() {
//     this.getItems();
//   }

// }

