import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { finalize } from 'rxjs';

import { environment } from '../../../../environments/environment';

import { Item } from './services/item';
import { ItemsService } from './services/items.service';

import { SafePipe } from './pipes/safe.pipe';

declare const bootstrap: any;

@Component({
  selector: 'app-boxoffice',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SafePipe
  ],
  templateUrl: './boxoffice.component.html',
  styleUrl: './boxoffice.component.css'
})
export class BoxofficeComponent implements OnInit {

  readonly items = signal<Item[]>([]);
  readonly loaded = signal(false);
  readonly player = signal('');
  readonly playerLoaded = signal(false);

  private modalPlayer: any;

  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly itemsService = inject(ItemsService);

  readonly formFilter = this.fb.group({
    shows: [true],
    movies: [true],
    clips: [true],
    games: [true]
  });

  ngOnInit(): void {
    this.getItems();
  }

  getItems(): void {

    this.loaded.set(false);

    this.itemsService
      .getItems(environment.urlMovies)
      .pipe(
        finalize(() => this.loaded.set(true))
      )
      .subscribe(items => {
        this.items.set(items);
      });

  }

  onSelectItemTrailer(item: Item): void {

    this.player.set(item.youtubeLink);
    this.playerLoaded.set(true);

    if (!this.modalPlayer) {

      this.modalPlayer = new bootstrap.Modal(
        document.getElementById('exampleModal'),
        {
          keyboard: true
        }
      );

      document
        .getElementById('exampleModal')
        ?.addEventListener(
          'hidden.bs.modal',
          this.onCloseModal.bind(this)
        );

    }

    this.modalPlayer.show();

  }

  onCloseModal(): void {

    this.player.set('');
    this.playerLoaded.set(false);

  }

  addItem(): void {
    this.router.navigate(['/movies', 0]);
  }

}


// import { Component, OnInit } from '@angular/core';
// import { Router } from '@angular/router';
// import { FormBuilder, FormGroup } from '@angular/forms';

// import { Item } from './services/item';
// import { ItemsService } from './services/items.service';
// import { environment } from '../../../../environments/environment';

// import { SafePipe } from './pipes/safe.pipe';
// import { CommonModule } from '@angular/common';

// declare const bootstrap: any;

// @Component({
//   selector: 'app-boxoffice',
//   imports: [SafePipe, CommonModule],
//   templateUrl: './boxoffice.component.html',
//   styleUrls: ['./boxoffice.component.css']
// })
// export class BoxofficeComponent implements OnInit {
//   itemsLoaded: boolean;
//   items: Item[];
//   player: string;
//   playerLoaded: boolean;
//   modalPlayer: any;

//   formFilter!: FormGroup;

//   constructor(
//     public router: Router,
//     private itemsService: ItemsService,
//     private fb: FormBuilder) {

//     this.formFilter = this.fb.group({
//       shows: [true],
//       movies: [true],
//       clips: [true],
//       games: [true],
//     });

//     this.player = '';
//     this.playerLoaded = false;

//     this.items = [];
//     this.itemsLoaded = false;

//     this.formFilter.setValue({
//       shows: true,
//       movies: true,
//       clips: true,
//       games: true,
//     });

//   }

//   ngOnInit(): void {
//     this.getItems();
//   }

//   getItems(): any {
//     const url = environment.urlMovies;
//     this.itemsService.getItems(url)
//       .subscribe(
//         items => {
//           this.itemsLoaded = true;
//           this.items = items;
//         }
//       );
//   }

//   onSelectItemTrailer(item: any) {
//     this.player = item.youtubeLink;
//     this.playerLoaded = true;
//     if (this.modalPlayer === undefined) {
//       this.modalPlayer = new bootstrap.Modal(document.getElementById('exampleModal'), {
//         keyboard: true
//       })
//       const selectPlayer = document.getElementById('exampleModal')
//       selectPlayer?.addEventListener('hidden.bs.modal', this.onCloseModal.bind(this));
//     }
//     this.modalPlayer?.show();
//   }

//   onCloseModal() {
//     this.player = '';
//     this.playerLoaded = false;
//   }

//   addItem() {
//     this.router.navigate(['/movies', 0]);
//   }

// }
