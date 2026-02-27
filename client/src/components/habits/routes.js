export default [
  {
    path: "/habits",
    name: "HabitList",
    component: () => import("./HabitList.vue")
  },
  {
    path: "/habits/new",
    name: "CreateHabit",
    component: () => import("./HabitForm.vue")
  },
  {
    path: "/habits/:id",
    name: "HabitDetail",
    component: () => import("./HabitDetail.vue")
  },
  {
    path: "/habits/:id/edit",
    name: "EditHabit",
    component: () => import("./HabitForm.vue")
  }
];
