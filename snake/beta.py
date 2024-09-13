import pygame
import sys
import random

# Inicializace Pygame
pygame.init()

# Barvy
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
RED = (213, 50, 80)
GREEN = (0, 255, 0)

# Rozměry okna
SCREEN_WIDTH = 600
SCREEN_HEIGHT = 400
screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption("Snake Game")

# Načtení tlačítek
easy_button_image = pygame.image.load("EASY_BUTTON.png")
normal_button_image = pygame.image.load("MEDIUM_BUTTON.png")
hard_button_image = pygame.image.load("HARD_BUTTON.png")

# Třída Button
class Button:
    def __init__(self, x, y, image):
        self.image = image
        self.rect = self.image.get_rect()
        self.rect.topleft = (x, y)

    def draw(self):
        screen.blit(self.image, (self.rect.x, self.rect.y))

    def is_clicked(self, pos):
        return self.rect.collidepoint(pos)

# Vytvoření tlačítek
easy_button = Button(100, 150, easy_button_image)
normal_button = Button(250, 150, normal_button_image)
hard_button = Button(400, 150, hard_button_image)

# Funkce pro spuštění hry s určitou rychlostí
def game_loop(snake_speed):
    snake_block = 10
    snake_list = []
    snake_length = 1

    x = SCREEN_WIDTH // 2
    y = SCREEN_HEIGHT // 2
    x_change = 0
    y_change = 0

    food_x = round(random.randrange(0, SCREEN_WIDTH - snake_block) / 10.0) * 10.0
    food_y = round(random.randrange(0, SCREEN_HEIGHT - snake_block) / 10.0) * 10.0

    clock = pygame.time.Clock()
    game_over = False

    while not game_over:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    x_change = -snake_block
                    y_change = 0
                elif event.key == pygame.K_RIGHT:
                    x_change = snake_block
                    y_change = 0
                elif event.key == pygame.K_UP:
                    y_change = -snake_block
                    x_change = 0
                elif event.key == pygame.K_DOWN:
                    y_change = snake_block
                    x_change = 0

        if x >= SCREEN_WIDTH or x < 0 or y >= SCREEN_HEIGHT or y < 0:
            game_over = True

        x += x_change
        y += y_change
        screen.fill(BLACK)

        pygame.draw.rect(screen, GREEN, [food_x, food_y, snake_block, snake_block])
        snake_head = [x, y]
        snake_list.append(snake_head)
        if len(snake_list) > snake_length:
            del snake_list[0]

        for block in snake_list[:-1]:
            if block == snake_head:
                game_over = True

        for segment in snake_list:
            pygame.draw.rect(screen, WHITE, [segment[0], segment[1], snake_block, snake_block])

        if x == food_x and y == food_y:
            food_x = round(random.randrange(0, SCREEN_WIDTH - snake_block) / 10.0) * 10.0
            food_y = round(random.randrange(0, SCREEN_HEIGHT - snake_block) / 10.0) * 10.0
            snake_length += 1

        pygame.display.flip()
        clock.tick(snake_speed)

    pygame.quit()
    sys.exit()

# Výběr obtížnosti
def main_menu():
    running = True
    while running:
        screen.fill(WHITE)

        easy_button.draw()
        normal_button.draw()
        hard_button.draw()

        pygame.display.flip()

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            if event.type == pygame.MOUSEBUTTONDOWN:
                if easy_button.is_clicked(event.pos):
                    game_loop(10)
                elif normal_button.is_clicked(event.pos):
                    game_loop(20)
                elif hard_button.is_clicked(event.pos):
                    game_loop(30)

    pygame.quit()
    sys.exit()

# Spuštění hlavního menu
main_menu()
