import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () => import('./core/layout/main-layout/main-layout').then(m => m.MainLayout),
        children: [
            {
                path: '',
                loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard)
            },
            {
                path: 'sparks',
                loadComponent: () => import('./features/sparks-dashboard/sparks-dashboard').then(m => m.SparksDashboard)
            },
            {
                path: 'subscriptions',
                loadComponent: () => import('./features/subscriptions/subscriptions').then(m => m.Subscriptions)
            },
            {
                path: 'watch/:id',
                loadComponent: () => import('./features/watch/watch').then(m => m.WatchComponent)
            }
        ]
    }
];
